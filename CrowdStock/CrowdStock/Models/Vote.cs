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
		public int Id { get; set; }

		[Index]
		public string UserId { get; set; }

		[Index]
		public string StockId { get; set; }

		public bool isPositive { get; set; }

		public DateTime Date { get; set; }

		public TimeSpan TimeSpan { get; set; }

		public virtual ApplicationUser User { get; set; }

		public virtual Stock Stock { get; set; }

		[NotMapped]
		[Display(Name="Correct")]
		public bool? IsCorrect
		{
			get
			{
				var endValue = (from hist in this.Stock.Histories
								where hist.Date >= this.Date + this.TimeSpan
								orderby hist.Date
								select hist.Value).FirstOrDefault();

				if(endValue == null)
					return null;

				var startValue = (from hist in this.Stock.Histories
								  where hist.Date <= this.Date
								  orderby hist.Date descending
								  select hist.Value).FirstOrDefault();

				return (isPositive ^ endValue < startValue) || (endValue != startValue); //if the prediction was correct or the final stock value is the same, the prediction is considered correct.

			}
		}
	}
}