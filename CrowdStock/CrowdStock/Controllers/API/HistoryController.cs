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
            if (history == null)
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
            if (histories == null)
            {
                return NotFound();
            }

            return Ok(histories);
        }

        // GET api/History/5
        [ResponseType(typeof(IEnumerable<History>))]
        public IHttpActionResult GetHistory(String id, int count)
        {
            var histories = db.Histories.Where(hist => hist.StockId == id).OrderByDescending(hist => hist.Date).Take(count);
            if (histories == null)
            {
                return NotFound();
            }

            return Ok(histories);
        }
    }
}