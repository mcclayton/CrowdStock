namespace CrowdStock.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class AddedImageFileType : DbMigration
    {
        public override void Up()
        {
            AddColumn("dbo.AspNetUsers", "ImageFileType", c => c.String());
        }
        
        public override void Down()
        {
            DropColumn("dbo.AspNetUsers", "ImageFileType");
        }
    }
}
