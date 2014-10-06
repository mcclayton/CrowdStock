using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Description;
using CrowdStock.Models;

namespace CrowdStock.Controllers.API
{
	public class StocksController : ApiController
	{
		private CrowdStockDBContext db = new CrowdStockDBContext();

		// GET: api/Stocks
		public IQueryable<StockApiViewModel> GetStocks()
		{
			var stocks = from stock in db.Stocks
						 orderby stock.Id
						 select new StockApiViewModel
						 {
							 Id = stock.Id,
							 Name = stock.Name,
							 Description = stock.Description,
							 LastHistory = stock.History.OrderByDescending(hist => hist.Date).FirstOrDefault()
						 };
			return stocks;
		}

		// GET: api/Stocks/5
		[ResponseType(typeof(Stock))]
		public async Task<IHttpActionResult> GetStock(string id)
		{
			Stock stock = await db.Stocks.FindAsync(id);
			if(stock == null)
			{
				return NotFound();
			}

			return Ok(new StockApiViewModel
			{
				Id = stock.Id,
				Name = stock.Name,
				Description = stock.Description,
				LastHistory = stock.History.OrderByDescending(hist => hist.Date).FirstOrDefault()
			});
		}
	}
}