using CrowdStock.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;

namespace CrowdStock.Controllers.API
{
	public class SearchController : ApiController
	{
		public IHttpActionResult GetSearch(string id, string type = "both")
		{
			var results = new List<object>();

			IQueryable<ApplicationUser> userResults;
			IQueryable<Stock> stockResults;

			string[] terms = id.Split(new char[] { ',', ' ' }, StringSplitOptions.RemoveEmptyEntries);

			using(var db = new CrowdStockDBContext())
			{
				userResults = db.Users;
				stockResults = db.Stocks;

				foreach(string term in terms)
				{
					userResults = from user in userResults
								  where user.UserName.ToUpper().Contains(term.ToUpper())
								  select user;

					stockResults = from stock in stockResults
								   where stock.Id.ToUpper().Contains(term.ToUpper())
								   || stock.Name.ToUpper().Contains(term.ToUpper())
								   select stock;
				}

				if(type.ToUpper() == "USERS" || type.ToUpper() == "BOTH")
					foreach(var user in userResults)
					{
						results.Add(new
						{
							Type = "user",
							Id = user.Id,
							Name = user.UserName
						});
					}

				if(type.ToUpper() == "STOCKS" || type.ToUpper() == "BOTH")
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