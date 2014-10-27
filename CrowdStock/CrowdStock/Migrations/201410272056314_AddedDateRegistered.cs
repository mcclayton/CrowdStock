namespace CrowdStock.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class AddedDateRegistered : DbMigration
    {
        public override void Up()
        {
            AddColumn("dbo.AspNetUsers", "DateRegistered", c => c.DateTime(nullable: false));
        }
        
        public override void Down()
        {
            DropColumn("dbo.AspNetUsers", "DateRegistered");
        }
    }
}
