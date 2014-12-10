using CrowdStock.Models;
using System.Linq;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Description;

namespace CrowdStock.Controllers.API
{
	public class StocksController : ApiController
	{
		private CrowdStockDBContext db = new CrowdStockDBContext();

		// GET: api/Stocks
		[Authorize]
		public IQueryable<StockApiViewModel> GetStocks()
		{
			var stocks = from stock in db.Stocks
						 orderby stock.Id
						 select new StockApiViewModel
						 {
							 Id = stock.Id,
							 Name = stock.Name,
                             Logo = stock.Logo,
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
                Logo = stock.Logo,
				Description = stock.Description,
				LastHistory = stock.History.OrderByDescending(hist => hist.Date).FirstOrDefault()
			});
		}
	}
}