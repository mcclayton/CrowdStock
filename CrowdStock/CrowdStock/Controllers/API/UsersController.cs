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
