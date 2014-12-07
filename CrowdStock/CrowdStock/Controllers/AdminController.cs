using CrowdStock.Models;
using PagedList;
using System.Linq;
using System.Web.Mvc;

namespace CrowdStock.Controllers
{
	[Authorize(Roles = "Administrator")]
	public class AdminController : Controller
	{
		private CrowdStockDBContext db = new CrowdStockDBContext();

		public ActionResult Index()
		{
			return View();
		}

		public ActionResult Users(int? page)
		{
			return View(db.Users.OrderBy(u => u.LastName).ThenBy(u => u.FirstName).ToPagedList(page ?? 1, 25));
		}

		public ActionResult UpdateAll()
		{
			UpdateReputations();
			UpdateConsensusAndOptimism();
			db.SaveChanges();
			return RedirectToAction("Index");
		}

		[NonAction]
		public void UpdateReputations()
		{
			var users = db.Users;
			foreach(var user in users)
			{
				user.UpdateReputation();
			}
		}

		[NonAction]
		public void UpdateConsensusAndOptimism()
		{
			var stocks = db.Stocks;
			foreach(var stock in db.Stocks)
			{
				stock.UpdateConsensus();
				stock.UpdateOptimism();
			}
		}
	}
}