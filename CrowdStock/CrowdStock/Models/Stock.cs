using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Runtime.Serialization;

namespace CrowdStock.Models
{
	public class Stock
	{
		[Key]
		[Required]
		[StringLength(4)]
		[Display(Name = "Symbol")]
		[RegularExpression("^[A-Z]+$", ErrorMessage = "Symbol must be UPPERCASE")]
		public string Id { get; set; }

		[Required]
		[StringLength(50)]
		[Index]
		public string Name { get; set; }

		public string Description { get; set; }

		[JsonIgnore] // json
		[IgnoreDataMember] //xml
		public virtual ICollection<History> History { get; set; }

		[JsonIgnore] // json
		[IgnoreDataMember] //xml
		public virtual ICollection<Vote> Votes { get; set; }

		/// <summary>
		/// A decimal value between 0 and 1 which indicates how many users think that the stock price will be going up
		/// </summary>
		[Display(Name = "% Consensus")]
		[DisplayFormat(DataFormatString = "{0:P}", ApplyFormatInEditMode = false)]
		public double Consensus { get; set; }

		public void UpdateConsensus()
		{
			if(this.Votes == null)
			{
				this.Consensus = 0;
				return;
			}

			var futureVotes = from vote in this.Votes
							  where vote.EndDate > DateTime.Now
							  select vote.isPositive ? 1 : 0;

			if(!futureVotes.Any())
			{
				this.Consensus = 0;
				return;
			}

			this.Consensus = futureVotes.Average();
		}

		/// <summary>
		/// A decimal value between 0 and 100 which indicates how many users think that the stock price will be going up and
		/// based on the user's reputation
		/// </summary>
		[Display(Name = "% Optimism")]
		public double Optimism { get; set; }

		public void UpdateOptimism()
		{
			if(this.Votes == null)
			{
				this.Optimism = 0;
				return;
			}

			var futureVotes = from vote in this.Votes
							  where vote.EndDate > DateTime.Now
							  select vote;
			if(!futureVotes.Any())
			{
				this.Optimism = 0;
				return;
			}

			//This is the optimism to be returned
			double optimism = 0.0;
			//This is the total reputation of all the votes
			double totalRepuatation = 0.0;

			foreach(Vote v in futureVotes)
			{
				totalRepuatation += v.User.Reputation;
			}

			foreach(Vote v in futureVotes)
			{
				if(v.isPositive)
				{
					optimism += (v.User.Reputation / totalRepuatation);
				}
			}

			//This changes the value from a percentage to a decimal value from 0 to 100
			this.Optimism = (optimism * 100.00);
		}

		public byte[] Logo { get; set; }
	}
}