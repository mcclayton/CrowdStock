using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Web;
using System.Xml.Serialization;
using System.IO;
using CrowdStockDBUpdater;
using System.Data.Entity.SqlServer;

namespace CrowdStockDBUpdater
{
	class Program
	{
		public static void DownloadData(string symbol, DateTime startDate, DateTime endDate)
		{
			var db = new CrowdStockDBContext();

			using(WebClient web = new WebClient())
			{
                string data = web.DownloadString(string.Format("http://query.yahooapis.com/v1/public/yql?q=select%20%2a%20from%20yahoo.finance.historicaldata%20where%20symbol%20in%20%28%27{0}%27%29%20and%20startDate%20=%20%27{1}%27%20and%20endDate%20=%20%27{2}%27&diagnostics=true&env=store://datatables.org/alltableswithkeys", symbol, startDate.ToString("yyyy-MM-dd"), endDate.ToString("yyyy-MM-dd")));

				int start = data.IndexOf("<results>") + 9;
				int symbolStart = data.IndexOf("\"", start) + 1;
				int symbolLen = data.IndexOf("\"", symbolStart) - symbolStart;
				string sym = data.Substring(symbolStart, symbolLen);
				start = data.IndexOf(">", symbolStart + symbolLen) + 1;
				int length = data.IndexOf("</quote>") - start;

				List<History> histories = new List<History>();
				List<Stock> stocks = new List<Stock>();

				while(start > 0)
				{
					string quoteData = "<Quote>" + data.Substring(start, length) + "</Quote>";

					XmlSerializer x = new XmlSerializer(typeof(Quote));
					TextReader reader = new StringReader(quoteData);
					Quote quote = (Quote)x.Deserialize(reader);
					reader.Close();

					History hist = new History();
					hist.Value = quote.Open;
					hist.StockId = sym;
					hist.Date = DateTime.Parse(quote.Date);
					hist.Stock = db.Stocks.Find(sym);
					if(hist.Stock == null)
					{
						for(int i = 0; i < stocks.Count; i++)
						{
							if(stocks[i].Name.Equals(sym))
							{
								hist.Stock = stocks[i];
								break;
							}
						}
						if(hist.Stock == null)
						{
							Stock stock = new Stock
							{
								Id = sym.ToUpper(),
								Name = sym
							};

							hist.Stock = stock;
							stocks.Add(hist.Stock);
						}
					}
					histories.Add(hist);

					start = data.IndexOf("<quote", (start + length));
					if(start < 0)
					{
						break;
					}
					symbolStart = data.IndexOf("\"", start) + 1;
					symbolLen = data.IndexOf("\"", symbolStart) - symbolStart;
					sym = data.Substring(symbolStart, symbolLen);
					start = data.IndexOf(">", symbolStart + symbolLen) + 1;
					length = data.IndexOf("</quote>", start) - start;
				}


				for(int i = 0; i < stocks.Count; i++)
				{
					if(db.Stocks.Find(stocks[i].Name) == null)
					{
						db.Stocks.Add(stocks[i]);
					}
					else
					{
						db.Entry(stocks[i]).CurrentValues.SetValues(stocks[i]);
					}
				}

				foreach(History h in histories)
				{
					var existingentries =
						from hist in db.Histories
						where hist.StockId == h.StockId
						&& SqlFunctions.DatePart("yyyy", hist.Date) == SqlFunctions.DatePart("yyyy", h.Date)
						&& SqlFunctions.DatePart("dy", hist.Date) == SqlFunctions.DatePart("dy", h.Date)
						select hist;

					db.Histories.RemoveRange(existingentries);

					db.Histories.Add(h);
				}
				db.SaveChanges();

			}

		}

		static string[] getTopStocks()
		{
			/*string[] symbols = {"TXRH", "ARC", "ETP", "HNH", "TTPH", "ESPR", "CTAS", "CEMP", "ADP", "RENT", "TTGT", "BABY", "TKMR", "THS", "PNRA", "CALD",
                                    "ASPX", "CP", "AMAG", "LTS", "FNHC", "ALXN", "JACK", "FLWS", "KNX", "SBCF", "NKE", "GPN", "NATH", "CHDN", "BSTC", "BAH",
                                    "VDSI", "PAYX", "TK", "RLGT", "VR", "OABC", "BREW", "ZTS", "MOVE", "STRP", "DTSI", "IG", "PANW", "LMNX", "SGNT", "ERIE",
                                    "HSNI", "GTS", "DSPG", "GS", "DVCR", "TIBX", "SONC", "RVP", "HAWKB", "AMBI", "ICUI", "ADSK", "NI", "ATV", "EAT", "CCRN",
                                    "VIMC", "LOGM", "AXDX", "HAWK", "EW", "SVVC", "QLYS", "ICLR", "CMRX", "TREE", "AGIO", "OMAB", "STRZA", "MNK", "CLCT", "N",
                                    "MFSF", "PPC", "FARM", "PDCO", "PFSW", "AYI", "TSN", "AGN", "TCP", "EPIQ", "LPDX", "CF", "EIGI", "HEP", "ATHL", "TEVA", "INSY",
                                    "ANCX", "DENN", "CFI"}; these are all 100 starting symbols */
            var db = new CrowdStockDBContext();
            
            var stocks =
                from symbol in db.Stocks
                select symbol.Id;
            string[] symbols = stocks.ToArray();
			return symbols;
		}

		static void Main(string[] args)
		{
			string[] symbols = getTopStocks();
            var db = new CrowdStockDBContext();

			DateTime startDate = DateTime.Now;
			DateTime endDate = DateTime.Now;

			//Get all of the new data
            foreach (string symbol in symbols){
                var st =
                    from hist in db.Histories
                    where hist.StockId.Equals(symbol)
                    orderby hist.Date descending
                    select hist;
                startDate = st.First().Date;
                DownloadData(symbol, startDate, endDate);
            }
		}
	}
}
