﻿using CrowdStock.Models;
using Microsoft.AspNet.Identity;
using System;
using System.Linq;
using System.Net;
using System.Web.Http;
using System.Web.Http.Description;

namespace CrowdStock.Controllers.API
{
	[Authorize]
	public class VotesController : ApiController
	{
		private CrowdStockDBContext db = new CrowdStockDBContext();

		// GET: api/Votes
		[HttpGet, Route("api/Votes/User/{id}")]
		public IHttpActionResult UserVotes(string id, int count = 100, int skip = 0)
		{
			var user = db.Users.Find(id);
			if(user == null)
				return NotFound();
			return Ok(user.Votes.OrderByDescending(v => v.Date).ThenByDescending(v => v.EndDate).Skip(skip).Take(count));
		}

		[HttpGet, Route("api/Votes/Stock/{id}")]
		public IHttpActionResult StockVotes(string id, int count = 100, int skip = 0)
		{
			var stock = db.Stocks.Find(id);
			if(stock == null)
				return NotFound();
			return Ok(stock.Votes.OrderByDescending(v => v.Date).ThenByDescending(v => v.EndDate).Skip(skip).Take(count));
		}

		// GET: api/Votes/5
		[ResponseType(typeof(Vote))]
		public IHttpActionResult GetVote(int id)
		{
			var vote = db.Votes.Find(id);
			if(vote == null)
			{
				return NotFound();
			}

			return Ok(vote);
		}

		[Route("api/Votes/UserStock")]
		public IHttpActionResult UserStock(string stockId, string userId, bool onlyActive = false)
		{
			var votes = from vote in db.Votes
						where vote.UserId == userId
							  && vote.StockId == stockId.ToUpper()
						select vote;

			if(onlyActive)
				votes = votes.Where(vote => vote.EndDate > DateTime.Now);

			return Ok(votes);
		}

		// POST: api/Votes
		[ResponseType(typeof(Vote))]
		public IHttpActionResult PostVote(ApiVoteViewModel vote)
		{
			if(!ModelState.IsValid)
			{
				return BadRequest(ModelState);
			}

			if(db.Users.Find(User.Identity.GetUserId()) == null)
				return NotFound();
			if(db.Stocks.Find(vote.StockId) == null)
				return NotFound();
			if(vote.EndDate < DateTime.Now.AddDays(1))
				throw new HttpResponseException(HttpStatusCode.PreconditionFailed);
			var userId = User.Identity.GetUserId();
			if(db.Votes.Any(v => v.UserId == userId && v.StockId == vote.StockId && v.EndDate >= DateTime.Now))
				throw new HttpResponseException(HttpStatusCode.PreconditionFailed);

			var newVote = new Vote
			{
				UserId = User.Identity.GetUserId(),
				StockId = vote.StockId,
				isPositive = vote.isPositive,
				Date = DateTime.Now,
				EndDate = vote.EndDate
			};

			db.Votes.Add(newVote);
			db.SaveChanges();

			return CreatedAtRoute("DefaultApi", new { id = newVote.Id }, newVote);
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