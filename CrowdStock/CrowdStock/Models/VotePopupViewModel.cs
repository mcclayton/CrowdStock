using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace CrowdStock.Models
{
	public enum VPType
	{
		CanVote,
		AlreadyVoted,
		VoteSubmitted,
		NotAuthorized
	}

	public class VotePopupViewModel
	{
		public VPType Type { get; set; }
		public string StockId { get; set; }

		[Display(Name="How many days?")]
		public int nDays { get; set; }

		[Display(Name="Up or Down?")]
		public bool isPositive { get; set; }
	}
}