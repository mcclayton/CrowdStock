using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Newtonsoft.Json;
using System.Runtime.Serialization;

namespace CrowdStock.Models
{
	public class Vote
	{
		[Key]
		public int Id { get; set; }

		[Index]
		[Required]
		public string UserId { get; set; }

		[Index]
		[Required]
		public string StockId { get; set; }

		[Required]
		public bool isPositive { get; set; }

		[Required]
		public DateTime Date { get; set; }

		[Required]
		public DateTime EndDate { get; set; }

		[JsonIgnore] //json
		[IgnoreDataMember] //xml
		public virtual ApplicationUser User { get; set; }

		[JsonIgnore] //json
		[IgnoreDataMember] //xml
		public virtual Stock Stock { get; set; }

		[NotMapped]
		[Display(Name = "Correct")]
		public bool? IsCorrect
		{
			get
			{
				var endHist = (from hist in this.Stock.History
								where hist.Date >= this.EndDate
								orderby hist.Date
								select hist).FirstOrDefault();

				if(endHist == null)
					return null;

				var startHist = (from hist in this.Stock.History
								  where hist.Date <= this.Date
								  orderby hist.Date descending
								  select hist).FirstOrDefault();

				return (isPositive ^ endHist.Value < startHist.Value) || (endHist.Value == startHist.Value); //if the prediction was correct or the final stock value is the same, the prediction is considered correct.

			}
		}
	}
}