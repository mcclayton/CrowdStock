namespace CrowdStock.Models
{
	using System;
	using System.Data.Entity;
	using System.ComponentModel.DataAnnotations.Schema;
	using System.Linq;

	public partial class CrowdStockDBContext : DbContext
	{
		public CrowdStockDBContext()
			: base("name=CrowdStockEntities")
		{
			Database.SetInitializer(new MigrateDatabaseToLatestVersion<CrowdStockDBContext,Migrations.Configuration>());
		}

		public virtual DbSet<Stock> Stocks { get; set; }
		public virtual DbSet<History> Histories { get; set; }

		protected override void OnModelCreating(DbModelBuilder modelBuilder)
		{
		}
	}
}
