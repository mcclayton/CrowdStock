namespace CrowdStock.Migrations
{
	using System.Data.Entity.Migrations;

	public partial class AddStockAndHistory : DbMigration
	{
		public override void Up()
		{
			CreateTable(
				"dbo.Histories",
				c => new
					{
						ID = c.Int(nullable: false, identity: true),
						StockID = c.String(nullable: false, maxLength: 4),
						Date = c.DateTime(nullable: false),
						Value = c.Single(nullable: false),
					})
				.PrimaryKey(t => t.ID)
				.ForeignKey("dbo.Stocks", t => t.StockID, cascadeDelete: true)
				.Index(t => t.StockID);

			CreateTable(
				"dbo.Stocks",
				c => new
					{
						ID = c.String(nullable: false, maxLength: 4),
						Name = c.String(nullable: false, maxLength: 50),
						Description = c.String(),
					})
				.PrimaryKey(t => t.ID);
		}

		public override void Down()
		{
			DropForeignKey("dbo.Histories", "StockID", "dbo.Stocks");
			DropIndex("dbo.Histories", new[] { "StockID" });
			DropTable("dbo.Stocks");
			DropTable("dbo.Histories");
		}
	}
}