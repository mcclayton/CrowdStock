using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace CrowdStock.Models
{
	public class Vote
	{
		[Key]
		public int ID { get; set; }

		[Index]
		public string UserID { get; set; }

		[Index]
		public string StockID { get; set; }

		public bool isPositive { get; set; }

		public DateTime Date { get; set; }

		public TimeSpan TimeSpan { get; set; }

		public virtual ApplicationUser User { get; set; }

		public virtual Stock Stock { get; set; }
	}
}