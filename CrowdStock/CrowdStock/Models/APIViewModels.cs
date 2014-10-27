using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CrowdStock.Models
{
	public class StockApiViewModel
	{
		public string Id { get; set; }
		public string Name { get; set; }
		public string Description { get; set; }
		public History LastHistory { get; set; }
	}

	public class APIAuthenticateViewModel
	{
		public string Name { get; set; }
		public string Password { get; set; }
	}
}