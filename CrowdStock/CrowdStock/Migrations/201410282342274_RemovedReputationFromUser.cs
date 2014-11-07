namespace CrowdStock.Migrations
{
	using System.Data.Entity.Migrations;

	public partial class RemovedReputationFromUser : DbMigration
	{
		public override void Up()
		{
			DropColumn("dbo.AspNetUsers", "Reputation");
		}

		public override void Down()
		{
			AddColumn("dbo.AspNetUsers", "Reputation", c => c.Double(nullable: false));
		}
	}
}