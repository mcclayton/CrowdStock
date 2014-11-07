using System;
using System.ComponentModel.DataAnnotations;

namespace CrowdStock.Models
{
	public class VoteViewModel
	{
		public bool VoteActive { get; set; }

		public int Id { get; set; }

		public string UserId { get; set; }

		public string StockId { get; set; }

		public bool isPositive { get; set; }

		public DateTime Date { get; set; }

		public DateTime EndDate { get; set; }

		public virtual ApplicationUser User { get; set; }

		public virtual Stock Stock { get; set; }

		[Display(Name = "Correct")]
		public bool? IsCorrect { get; set; }
	}
}