namespace CrowdStock.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class AddedVotes : DbMigration
    {
        public override void Up()
        {
            CreateTable(
                "dbo.Votes",
                c => new
                    {
                        ID = c.Int(nullable: false, identity: true),
                        UserID = c.String(maxLength: 128),
                        StockID = c.String(maxLength: 4),
                        isPositive = c.Boolean(nullable: false),
                        Date = c.DateTime(nullable: false),
                        TimeSpan = c.Time(nullable: false, precision: 7),
                    })
                .PrimaryKey(t => t.ID)
                .ForeignKey("dbo.Stocks", t => t.StockID)
                .ForeignKey("dbo.AspNetUsers", t => t.UserID)
                .Index(t => t.UserID)
                .Index(t => t.StockID);
            
        }
        
        public override void Down()
        {
            DropForeignKey("dbo.Votes", "UserID", "dbo.AspNetUsers");
            DropForeignKey("dbo.Votes", "StockID", "dbo.Stocks");
            DropIndex("dbo.Votes", new[] { "StockID" });
            DropIndex("dbo.Votes", new[] { "UserID" });
            DropTable("dbo.Votes");
        }
    }
}
