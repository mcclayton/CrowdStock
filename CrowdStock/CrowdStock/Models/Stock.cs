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
		[NotMapped]
		[Display(Name = "% Consensus")]
		[DisplayFormat(DataFormatString = "{0:P}", ApplyFormatInEditMode = false)]
		public double Consensus
		{
			get
			{
				if(this.Votes == null)
					return 0;

				var futureVotes = from vote in this.Votes
								  where vote.EndDate > DateTime.Now
								  select vote.isPositive ? 1 : 0;

				if(!futureVotes.Any())
					return 0;

				return futureVotes.Average();
			}
		}

		/// <summary>
		/// A decimal value between 0 and 100 which indicates how many users think that the stock price will be going up and
		/// based on the user's reputation
		/// </summary>
		[NotMapped]
		[Display(Name = "% Optimism")]
		public double Optimism
		{
			get
			{
				if(this.Votes == null)
					return 0;

				var futureVotes = from vote in this.Votes
								  where vote.EndDate > DateTime.Now
								  select vote;
				if(!futureVotes.Any())
					return 0;

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
				return (optimism * 100.00);
			}
		}
	}
}