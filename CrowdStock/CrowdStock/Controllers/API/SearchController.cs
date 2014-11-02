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

			IEnumerable<ApplicationUser> userResults = new List<ApplicationUser>();
			IEnumerable<Stock> stockResults = new List<Stock>();

			string[] terms = id.Split(new char[] { ',', ' ' }, StringSplitOptions.RemoveEmptyEntries);

			using(var db = new CrowdStockDBContext())
			{
				foreach(string term in terms)
				{
					if(type.ToUpper() == "USERS" || type.ToUpper() == "BOTH")
					{
						var users = from user in db.Users
									where user.UserName.ToUpper().Contains(term.ToUpper())
									select user;
						userResults = userResults.Union(users);
					}

					if(type.ToUpper() == "STOCKS" || type.ToUpper() == "BOTH")
					{
						var stocks = from stock in db.Stocks
									 where stock.Id.ToUpper().Contains(term.ToUpper())
									 || stock.Name.ToUpper().Contains(term.ToUpper())
									 select stock;
						stockResults = stockResults.Union(stocks);
					}
				}

				foreach(var user in userResults)
				{
					results.Add(new
					{
						Type = "user",
						Id = user.Id,
						Name = user.UserName
					});
				}

				foreach(var stock in stockResults)
				{
					results.Add(new
					{
						Type = "stock",
						Id = stock.Id,
						Name = stock.Name
					});
				}

			}

			return Ok(results);
		}
	}
}
