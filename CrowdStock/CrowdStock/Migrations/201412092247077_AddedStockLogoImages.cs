namespace CrowdStock.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class AddedStockLogoImages : DbMigration
    {
        public override void Up()
        {
            AddColumn("dbo.Stocks", "Logo", c => c.Binary());
        }
        
        public override void Down()
        {
            DropColumn("dbo.Stocks", "Logo");
        }
    }
}
