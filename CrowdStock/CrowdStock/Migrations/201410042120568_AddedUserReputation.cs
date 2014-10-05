namespace CrowdStock.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class AddedUserReputation : DbMigration
    {
        public override void Up()
        {
            AddColumn("dbo.AspNetUsers", "Reputation", c => c.Double(nullable: false));
        }
        
        public override void Down()
        {
            DropColumn("dbo.AspNetUsers", "Reputation");
        }
    }
}
