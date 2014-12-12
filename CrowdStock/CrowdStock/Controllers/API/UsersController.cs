using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Net;
using System.Web;
using CrowdStock.Models;
using System.Web.Http;

namespace CrowdStock.Controllers.API
{
	public class UsersController : ApiController
	{
		private CrowdStockDBContext db = new CrowdStockDBContext();

		[HttpGet, Route("api/User/Id/{id}")]
		public IHttpActionResult FindById(string id)
		{
			var user = db.Users.Find(id);

			if(user == null)
				return NotFound();

			return Ok(new ApiUserInfoViewModel
			{
				Id = user.Id,
				Name = user.UserName,
				Reputation = user.Reputation,
				AverageScore = user.AverageScore,
				nVotes = user.Votes.Count
			});
		}

		[HttpGet, Route("api/Users/Top/{count}")]
		public IHttpActionResult GetTopUsers(int count)
		{
			var users = from user in db.Users
						orderby user.Reputation descending
						select new ApiUserInfoViewModel
						{
							Id = user.Id,
							Name = user.UserName,
							Reputation = user.Reputation,
							AverageScore = -1, //TODO: move average score to database column and recalculate as needed
							nVotes = user.Votes.Count
						};

			users = users.Take(count);

			return Ok(users);
		}

		[HttpGet, Route("api/User/Name/{name}")]
		public IHttpActionResult FindByName(string name)
		{
			var user = db.Users.Where(u => u.UserName.ToUpper() == name.ToUpper()).SingleOrDefault();

			if(user == null)
				return NotFound();

			return Ok(new ApiUserInfoViewModel
			{
				Id = user.Id,
				Name = user.UserName,
				Reputation = user.Reputation,
				AverageScore = user.AverageScore,
				nVotes = user.Votes.Count
			});
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
