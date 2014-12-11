using CrowdStock.Models;
using Microsoft.AspNet.Identity;
using PagedList;
using System;
using System.Data.Entity;
using System.Linq;
using System.Net;
using System.Threading.Tasks;
using System.Web.Mvc;

namespace CrowdStock.Controllers
{
	public class StocksController : Controller
	{
		private CrowdStockDBContext db = new CrowdStockDBContext();

		// GET: Stocks
		public ActionResult Index()
		{
			ViewBag.TopStocks = (from stock in db.Stocks.ToList()
								 orderby stock.Optimism descending
								 select stock).Take(25);

			ViewBag.TopUsers = (from user in db.Users.ToList()
								orderby user.Reputation descending
								select user).Take(25);

			return View();
		}

		public ActionResult List(int? page)
		{
			var stocks = db.Stocks.OrderBy(s => s.Id);
			return View(stocks.ToPagedList(page ?? 1, 25));
		}
		public ActionResult ListUsers(int? page)
		{
			var users = db.Users.OrderBy(user => user.Reputation);
			return View(users.ToPagedList(page ?? 1, 25));
		}

        [AllowAnonymous]
        public ActionResult Logo(string id)
        {
            if (string.IsNullOrWhiteSpace(id))
                return HttpNotFound();

            using (var db = new CrowdStockDBContext())
            {
                var stock = db.Stocks.Find(id);
                if (stock == null)
                    return HttpNotFound();
                if (stock.Logo != null)
                    return File(stock.Logo, "image/gif");
                else
                    return File(Server.MapPath("~/Content/img/defaultImage.jpg"), "image/gif");
            }
        }

		// GET: Stocks/Details/5
		public async Task<ActionResult> Details(string id)
		{
			if(id == null)
			{
				return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
			}
			Stock stock = await db.Stocks.FindAsync(id);
			if(stock == null)
			{
				return HttpNotFound();
			}

			var user = db.Users.Find(User.Identity.GetUserId());
			if(user == null)
			{
				ViewBag.CurrentVote = new VoteViewModel();
			}
			else
			{
				var currentVote = (from vote in user.Votes
								   where vote.StockId.ToUpper() == id.ToUpper()
								   && vote.EndDate > DateTime.Now
								   select vote).SingleOrDefault();

				if(currentVote == null)
				{
					ViewBag.CurrentVote = new VoteViewModel { VoteActive = false, StockId = id.ToUpper() };
				}
				else
				{
					ViewBag.CurrentVote = new VoteViewModel
					{
						VoteActive = true,
						Id = currentVote.Id,
						UserId = currentVote.UserId,
						StockId = currentVote.StockId,
						isPositive = currentVote.isPositive,
						Date = currentVote.Date,
						EndDate = currentVote.EndDate,
						User = currentVote.User,
						Stock = currentVote.Stock,
						IsCorrect = currentVote.IsCorrect
					};
				}
			}

			return View(stock);
		}

		// GET: Stocks/Create
		[Authorize(Roles = "Administrator")]
		public ActionResult Create()
		{
			return View();
		}

		// POST: Stocks/Create
		// To protect from overposting attacks, please enable the specific properties you want to bind to, for
		// more details see http://go.microsoft.com/fwlink/?LinkId=317598.
		[HttpPost]
		[ValidateAntiForgeryToken]
		[Authorize(Roles = "Administrator")]
		public async Task<ActionResult> Create([Bind(Include = "Id,Name,Description")] Stock stock)
		{
			if(ModelState.IsValid)
			{
				db.Stocks.Add(stock);
				await db.SaveChangesAsync();
				return RedirectToAction("List");
			}

			return View(stock);
		}

		// GET: Stocks/Edit/5
		[Authorize(Roles = "Administrator")]
		public async Task<ActionResult> Edit(string id)
		{
			if(id == null)
			{
				return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
			}
			Stock stock = await db.Stocks.FindAsync(id);
			if(stock == null)
			{
				return HttpNotFound();
			}
			return View(stock);
		}

		// POST: Stocks/Edit/5
		// To protect from overposting attacks, please enable the specific properties you want to bind to, for
		// more details see http://go.microsoft.com/fwlink/?LinkId=317598.
		[HttpPost]
		[ValidateAntiForgeryToken]
		[Authorize(Roles = "Administrator")]
		public async Task<ActionResult> Edit([Bind(Include = "Id,Name,Description")] Stock stock)
		{
			if(ModelState.IsValid)
			{
				db.Entry(stock).State = EntityState.Modified;
				await db.SaveChangesAsync();
				return RedirectToAction("List");
			}
			return View(stock);
		}

		// GET: Stocks/Delete/5
		[Authorize(Roles = "Administrator")]
		public async Task<ActionResult> Delete(string id)
		{
			if(id == null)
			{
				return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
			}
			Stock stock = await db.Stocks.FindAsync(id);
			if(stock == null)
			{
				return HttpNotFound();
			}
			return View(stock);
		}

		// POST: Stocks/Delete/5
		[HttpPost, ActionName("Delete")]
		[ValidateAntiForgeryToken]
		[Authorize(Roles = "Administrator")]
		public async Task<ActionResult> DeleteConfirmed(string id)
		{
			Stock stock = await db.Stocks.FindAsync(id);
			db.Stocks.Remove(stock);
			await db.SaveChangesAsync();
			return RedirectToAction("List");
		}

		public async Task<ActionResult> VotePopup(string id)
		{
			if(!User.Identity.IsAuthenticated)
				return View(new VotePopupViewModel { Type = VPType.NotAuthorized });

			var stock = await db.Stocks.FindAsync(id.ToUpper());
			if(stock == null)
				return HttpNotFound();

			var currentVotes = from vote in stock.Votes
							   where vote.UserId == User.Identity.GetUserId()
							   && vote.EndDate > DateTime.Now
							   select vote;

			if(currentVotes.Any())
				return View(new VotePopupViewModel { Type = VPType.AlreadyVoted });

			return View(new VotePopupViewModel
			{
				Type = VPType.CanVote,
				StockId = stock.Id.ToUpper()
			});
		}

		[HttpPost]
		public async Task<ActionResult> VotePopup(VotePopupViewModel model)
		{
			if(!User.Identity.IsAuthenticated)
				return View(new VotePopupViewModel { Type = VPType.NotAuthorized });

			var stock = await db.Stocks.FindAsync(model.StockId.ToUpper());
			if(stock == null)
				return HttpNotFound();

			var currentVotes = from vote in stock.Votes
							   where vote.UserId == User.Identity.GetUserId()
							   && vote.EndDate > DateTime.Now
							   select vote;

			if(currentVotes.Any())
				return View(new VotePopupViewModel { Type = VPType.AlreadyVoted });

			if(model.nDays <= 0)
			{
				ModelState.AddModelError("", "Days must be > 0");
				return View(model);
			}

			Vote newVote = new Vote
			{
				Date = DateTime.Now,
				EndDate = DateTime.Now.AddDays(model.nDays),
				StockId = stock.Id,
				UserId = User.Identity.GetUserId(),
				isPositive = model.isPositive
			};
			db.Votes.Add(newVote);
			db.SaveChanges();

			model.Type = VPType.VoteSubmitted;
			return View(model);
		}

		protected override void Dispose(bool disposing)
		{
			if(disposing)
			{
				db.Dispose();
			}
			base.Dispose(disposing);
		}
	}
}