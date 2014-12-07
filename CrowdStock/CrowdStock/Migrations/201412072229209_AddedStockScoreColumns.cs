namespace CrowdStock.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class AddedStockScoreColumns : DbMigration
    {
        public override void Up()
        {
            AddColumn("dbo.Stocks", "Consensus", c => c.Double(nullable: false));
            AddColumn("dbo.Stocks", "Optimism", c => c.Double(nullable: false));
        }
        
        public override void Down()
        {
            DropColumn("dbo.Stocks", "Optimism");
            DropColumn("dbo.Stocks", "Consensus");
        }
    }
}
