namespace CrowdStock.Migrations
{
	using CrowdStock.Models;
	using Microsoft.AspNet.Identity;
	using Microsoft.AspNet.Identity.EntityFramework;
	using System.Data.Entity.Migrations;
	using System.Linq;

	internal sealed class Configuration : DbMigrationsConfiguration<CrowdStock.Models.CrowdStockDBContext>
	{
		public Configuration()
		{
			AutomaticMigrationsEnabled = false;
		}

		protected override void Seed(CrowdStock.Models.CrowdStockDBContext context)
		{
			//  This method will be called after migrating to the latest version.

			//  You can use the DbSet<T>.AddOrUpdate() helper extension method
			//  to avoid creating duplicate seed data. E.g.
			//
			//    context.People.AddOrUpdate(
			//      p => p.FullName,
			//      new Person { FullName = "Andrew Peters" },
			//      new Person { FullName = "Brice Lambson" },
			//      new Person { FullName = "Rowan Miller" }
			//    );

			if(!context.Roles.Any())
			{
				var roleStore = new RoleStore<IdentityRole>(context);
				var roleManager = new RoleManager<IdentityRole>(roleStore);
				var role = new IdentityRole
				{
					Name = "Administrator"
				};
				roleManager.Create(role);
			}

			if(!context.Users.Any())
			{
				var userStore = new UserStore<ApplicationUser>(context);
				var userManager = new ApplicationUserManager(userStore);

				var user = new ApplicationUser
				{
					Email = "admin@billking.io",
					UserName = "Admin",
					EmailConfirmed = true
				};
				userManager.Create(user, "BrandanMillerDotCom");
				userManager.AddToRole(user.Id, "Administrator");
			}
		}
	}
}