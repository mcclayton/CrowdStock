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
	public class HistoryController : ApiController
	{
		private CrowdStockDBContext db = new CrowdStockDBContext();

		// GET api/History/5
		[ResponseType(typeof(History))]
		public IHttpActionResult GetHistory(int id)
		{
			History history = db.Histories.Find(id);
			if(history == null)
			{
				return NotFound();
			}

			return Ok(history);
		}

		// GET api/History/5
		[ResponseType(typeof(IEnumerable<History>))]
		public IHttpActionResult GetHistory()
		{
			var histories = from hist in db.Histories
							group hist by hist.StockId into stockhist
							orderby stockhist.Key ascending
							select stockhist.OrderByDescending(hist => hist.Date).FirstOrDefault();
			if(histories == null)
			{
				return NotFound();
			}

			return Ok(histories);
		}

		// GET api/History/5
		[ResponseType(typeof(IEnumerable<History>))]
		public IHttpActionResult GetHistory(string stock, int? count = null)
		{
			IEnumerable<string> stocks = stock.Split(new[] { ',' }, StringSplitOptions.RemoveEmptyEntries).Select(s => s.ToUpper());

			var histories = new List<IQueryable<History>>();

			foreach(string symbol in stocks)
			{
				IQueryable<History> stockHistory = from hist in db.Histories
												   where hist.StockId == symbol
												   orderby hist.Date descending
												   select hist;

				if(count.HasValue)
					stockHistory = stockHistory.Take(count.Value);

				histories.Add(stockHistory);
			}

			if(!histories.Any())
			{
				return NotFound();
			}

			return Ok(histories);
		}
	}
}