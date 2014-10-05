using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Web;
using System.Xml.Serialization;
using System.IO;
using CrowdStock.Models;

namespace CrowdStockDb
{
    class Program
    {
         public static void DownloadData(string symbol, string startDate, string endDate)
        {
 
            using (WebClient web = new WebClient())
            {
                string data = web.DownloadString(string.Format("http://query.yahooapis.com/v1/public/yql?q=select%20%2a%20from%20yahoo.finance.historicaldata%20where%20symbol%20in%20%28{0}%29%20and%20startDate%20=%20%27{1}%27%20and%20endDate%20=%20%27{2}%27&diagnostics=true&env=store://datatables.org/alltableswithkeys", symbol, startDate, endDate));
                
                int start = data.IndexOf("<results>") + 9;
                int symbolStart = data.IndexOf("\"", start) + 1;
                int symbolLen = data.IndexOf("\"", symbolStart) - symbolStart;
                string sym = data.Substring(symbolStart, symbolLen);
                start = data.IndexOf(">", symbolStart + symbolLen) + 1;
                int length = data.IndexOf("</quote>") - start;

                List<History> histories = new List<History>();

                while (start > 0)
                {
                    string quoteData = "<Quote>" + data.Substring(start, length) + "</Quote>";

                    XmlSerializer x = new XmlSerializer(typeof(Quote));
                    TextReader reader = new StringReader(quoteData);
                    Quote quote = (Quote)x.Deserialize(reader);
                    reader.Close();

                    History hist = new History();
                    hist.Value = quote.Open;
                    hist.StockId = sym;
                    hist.Date = DateTime.Parse("yyyy-MM-dd");
                    histories.Add(hist);

                    start = data.IndexOf("<quote", (start + length));
                    if (start < 0)
                    {
                        break;
                    }
                    symbolStart = data.IndexOf("\"", start) + 1;
                    symbolLen = data.IndexOf("\"", symbolStart) - symbolStart;
                    sym = data.Substring(symbolStart, symbolLen);
                    start = data.IndexOf(">", symbolStart + symbolLen) + 1;
                    length = data.IndexOf("</quote>", start) - start;
                }
                /*
                 * db.History.add(history);
                 * db.saveChanges();

                 */
            }

        }

         static string generateSymbolString(string[] symbols)
         {
             StringBuilder sb = new StringBuilder();
             if (symbols == null)
             {
                 return null;
             }
             sb.Append("%27" + symbols[0] + "%27");
             for (int i = 1; i < symbols.Length; i++)
             {
                 sb.Append(",%20%27" + symbols[i] + "%27");
             }
             return sb.ToString();
         }

         static string[] getTopStocks()
         {
             string[] symbols = {"YHOO", "MSFT"};
             return symbols;
         }

         static string generateStartDate()
         {
             DateTime dt = DateTime.Now;
             dt = dt.AddMonths(-1);
             return dt.ToString("yyyy-MM-dd");
         }

         static string generateEndDate()
         {
             DateTime dt = DateTime.Now;  
             return dt.ToString("yyyy-MM-dd");
         }

        static void Main(string[] args)
        {
            string[] symbols = getTopStocks();
            DownloadData(generateSymbolString(symbols), generateStartDate(), generateEndDate());

        }
    }
}
