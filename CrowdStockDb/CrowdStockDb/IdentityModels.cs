using System.Security.Claims;
using System.Threading.Tasks;
using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.EntityFramework;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System;
using System.Data.Entity.SqlServer;

namespace CrowdStock.Models
{
	// You can add profile data for the user by adding more properties to your ApplicationUser class, please visit http://go.microsoft.com/fwlink/?LinkID=317594 to learn more.
	public class ApplicationUser : IdentityUser
	{
		public async Task<ClaimsIdentity> GenerateUserIdentityAsync(UserManager<ApplicationUser> manager)
		{
			// Note the authenticationType must match the one defined in CookieAuthenticationOptions.AuthenticationType
			var userIdentity = await manager.CreateIdentityAsync(this, DefaultAuthenticationTypes.ApplicationCookie);
			return userIdentity;
		}

		[Display(Name = "First Name")]
		[StringLength(15)]
		public string FirstName { get; set; }

		[Display(Name = "Last Name")]
		[StringLength(15)]
		public string LastName { get; set; }

		[NotMapped]
		[Display(Name = "Full Name")]
		public string FullName
		{
			get
			{
				return string.Format("{0} {1}", FirstName, LastName);
			}
		}

		[NotMapped]
		[Display(Name = "Average Score")]
		public double AverageScore
		{
			get
			{
				var recentVotes = (from vote in this.Votes
								   where vote.Date >= DateTime.Now.AddMonths(-6)
								   select vote).ToList();
				var results = from vote in recentVotes
							  where vote.IsCorrect.HasValue
							  select vote.IsCorrect.Value ? 1 : 0;

				if(!results.Any())
					return 0;

				return results.Average();
			}
		}

		public double Reputation { get; set; }

		public virtual ICollection<Vote> Votes { get; set; }
	}
}