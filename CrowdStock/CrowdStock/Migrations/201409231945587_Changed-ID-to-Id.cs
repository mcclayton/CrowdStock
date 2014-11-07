namespace CrowdStock.Migrations
{
	using System.Data.Entity.Migrations;

	public partial class ChangedIDtoId : DbMigration
	{
		public override void Up()
		{
			DropIndex("dbo.Histories", new[] { "StockID" });
			AlterColumn("dbo.AspNetUsers", "FirstName", c => c.String(maxLength: 15));
			AlterColumn("dbo.AspNetUsers", "LastName", c => c.String(maxLength: 15));
			CreateIndex("dbo.Histories", "StockId");
		}

		public override void Down()
		{
			DropIndex("dbo.Histories", new[] { "StockId" });
			AlterColumn("dbo.AspNetUsers", "LastName", c => c.String());
			AlterColumn("dbo.AspNetUsers", "FirstName", c => c.String());
			CreateIndex("dbo.Histories", "StockID");
		}
	}
}