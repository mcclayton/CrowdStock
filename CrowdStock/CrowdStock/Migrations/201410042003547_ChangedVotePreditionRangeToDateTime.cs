namespace CrowdStock.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class ChangedVotePreditionRangeToDateTime : DbMigration
    {
        public override void Up()
        {
            AddColumn("dbo.Votes", "EndDate", c => c.DateTime(nullable: false));
            DropColumn("dbo.Votes", "TimeSpan");
        }
        
        public override void Down()
        {
            AddColumn("dbo.Votes", "TimeSpan", c => c.Time(nullable: false, precision: 7));
            DropColumn("dbo.Votes", "EndDate");
        }
    }
}
