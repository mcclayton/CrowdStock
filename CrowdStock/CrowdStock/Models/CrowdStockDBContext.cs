namespace CrowdStock.Models
{
	using System;
	using System.Data.Entity;
	using System.ComponentModel.DataAnnotations.Schema;
	using System.Linq;
	using Microsoft.AspNet.Identity.EntityFramework;

	public partial class CrowdStockDBContext : IdentityDbContext<ApplicationUser>
	{
		public CrowdStockDBContext()
			: base("name=CrowdStockEntities")
		{
			Database.SetInitializer(new MigrateDatabaseToLatestVersion<CrowdStockDBContext, Migrations.Configuration>());
		}

        public static CrowdStockDBContext Create()
        {
            return new CrowdStockDBContext();
        }

		public virtual DbSet<Stock> Stocks { get; set; }
		public virtual DbSet<History> Histories { get; set; }

		protected override void OnModelCreating(DbModelBuilder modelBuilder)
		{
			base.OnModelCreating(modelBuilder);
		}
	}
}
