namespace CrowdStock.Migrations
{
	using System.Data.Entity.Migrations;

	public partial class AddStockIndices : DbMigration
	{
		public override void Up()
		{
			DropIndex("dbo.Histories", new[] { "StockID" });
			CreateIndex("dbo.Histories", "StockID");
			CreateIndex("dbo.Histories", "Date");
			CreateIndex("dbo.Stocks", "Name");
		}

		public override void Down()
		{
			DropIndex("dbo.Stocks", new[] { "Name" });
			DropIndex("dbo.Histories", new[] { "Date" });
			DropIndex("dbo.Histories", new[] { "StockID" });
			CreateIndex("dbo.Histories", "StockID");
		}
	}
}