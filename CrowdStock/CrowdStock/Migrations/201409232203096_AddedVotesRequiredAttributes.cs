namespace CrowdStock.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class AddedVotesRequiredAttributes : DbMigration
    {
        public override void Up()
        {
            DropForeignKey("dbo.Votes", "StockId", "dbo.Stocks");
            DropForeignKey("dbo.Votes", "UserId", "dbo.AspNetUsers");
            DropIndex("dbo.Votes", new[] { "UserId" });
            DropIndex("dbo.Votes", new[] { "StockId" });
            AlterColumn("dbo.Votes", "UserId", c => c.String(nullable: false, maxLength: 128));
            AlterColumn("dbo.Votes", "StockId", c => c.String(nullable: false, maxLength: 4));
            CreateIndex("dbo.Votes", "UserId");
            CreateIndex("dbo.Votes", "StockId");
            AddForeignKey("dbo.Votes", "StockId", "dbo.Stocks", "Id", cascadeDelete: true);
            AddForeignKey("dbo.Votes", "UserId", "dbo.AspNetUsers", "Id", cascadeDelete: true);
        }
        
        public override void Down()
        {
            DropForeignKey("dbo.Votes", "UserId", "dbo.AspNetUsers");
            DropForeignKey("dbo.Votes", "StockId", "dbo.Stocks");
            DropIndex("dbo.Votes", new[] { "StockId" });
            DropIndex("dbo.Votes", new[] { "UserId" });
            AlterColumn("dbo.Votes", "StockId", c => c.String(maxLength: 4));
            AlterColumn("dbo.Votes", "UserId", c => c.String(maxLength: 128));
            CreateIndex("dbo.Votes", "StockId");
            CreateIndex("dbo.Votes", "UserId");
            AddForeignKey("dbo.Votes", "UserId", "dbo.AspNetUsers", "Id");
            AddForeignKey("dbo.Votes", "StockId", "dbo.Stocks", "Id");
        }
    }
}
