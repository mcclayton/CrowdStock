using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(CrowdStock.Startup))]

namespace CrowdStock
{
	public partial class Startup
	{
		public void Configuration(IAppBuilder app)
		{
			ConfigureAuth(app);
		}
	}
}