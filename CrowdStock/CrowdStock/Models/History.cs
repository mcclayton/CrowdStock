using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace CrowdStock.Models
{
	public class History
	{
		[Key]
		[Required]
		public int ID { get; set; }

		[Required]
		[StringLength(4)]
		public string StockID { get; set; }

		[Required]
		public DateTime Date { get; set; }

		[Required]
		public float Value { get; set; }

		public virtual Stock Stock { get; set; }
	}
}