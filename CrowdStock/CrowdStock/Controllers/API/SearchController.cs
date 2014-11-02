using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using CrowdStock.Models;

namespace CrowdStock.Controllers.API
{
	public class SearchController : ApiController
	{
		public IHttpActionResult GetSearch(string id, string type = "both")
		{
			var results = new List<object>();

			using(var db = new CrowdStockDBContext())
			{
				if(type.ToUpper() == "USERS" || type.ToUpper() == "BOTH")
				{
					var users = from user in db.Users
								where user.UserName.ToUpper().Contains(id.ToUpper())
								select new
								{
									Type = "user",
									Id = user.Id,
									Name = user.UserName
								};
					results.AddRange(users);
				}

				if(type.ToUpper() == "STOCKS" || type.ToUpper() == "BOTH")
				{
					var stocks = from stock in db.Stocks
								 where stock.Id.ToUpper().Contains(id.ToUpper())
								 || stock.Name.ToUpper().Contains(id.ToUpper())
								 select new
								 {
									 Type = "stock",
									 Id = stock.Id,
									 Name = stock.Name
								 };
					results.AddRange(stocks);
				}
			}

			return Ok(results);
		}
	}
}
