using System;

namespace CrowdStock.Models
{
	public class StockApiViewModel
	{
		public string Id { get; set; }

		public string Name { get; set; }

        public byte[] Logo { get; set; }

		public string Description { get; set; }

		public History LastHistory { get; set; }
	}

	public class ApiRegisterViewModel
	{
		public string UserName { get; set; }
		public string Email { get; set; }
		public string Password { get; set; }
		public string FirstName { get; set; }
		public string LastName { get; set; }
	}

	public class ApiAuthenticateViewModel
	{
		public string Name { get; set; }

		public string Password { get; set; }
	}

	public class ApiVoteViewModel
	{
		public string StockId { get; set; }

		public bool isPositive { get; set; }

		public DateTime EndDate { get; set; }
	}

	public class ApiUserInfoViewModel
	{
		public string Id { get; set; }
		public string Name { get; set; }
		public double Reputation { get; set; }
		public double AverageScore { get; set; }
		public double nVotes { get; set; }
	}
}