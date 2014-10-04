﻿using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Web;

namespace CrowdStock.Models
{
	public class Stock
	{
		[Key]
		[Required]
		[StringLength(4)]
		[Display(Name="Symbol")]
		public string Id { get; set; }

		[Required]
		[StringLength(50)]
		[Index]
		public string Name { get; set; }

		public string Description { get; set; }

		public virtual ICollection<History> History { get; set; }

		public virtual ICollection<Vote> Votes { get; set; }

		/// <summary>
		/// A decimal value between 0 and 1 which indicates how many users think that the stock price will be going up
		/// </summary>
		[NotMapped]
		[Display(Name="% Optimism")]
		[DisplayFormat(DataFormatString="{0:P}",ApplyFormatInEditMode=false)]
		public double Consensus
		{
			get
			{
				var futureVotes = from vote in this.Votes
								  where vote.EndDate > DateTime.Now
								  select vote.isPositive ? 1 : 0;

				return futureVotes.Average();
			}
		}
	}
}