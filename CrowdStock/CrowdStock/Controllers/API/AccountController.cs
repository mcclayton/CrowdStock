using Microsoft.Owin.Infrastructure;
using Microsoft.Owin.Security;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Security.Claims;
using System.Web.Http;
using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.Owin;
using System.Web;
using CrowdStock.Models;

namespace CrowdStock.Controllers.API
{
    public class AccountController : ApiController
	{
		private ApplicationUserManager _userManager;
		public ApplicationUserManager UserManager
		{
			get
			{
				return _userManager ?? HttpContext.Current.GetOwinContext().GetUserManager<ApplicationUserManager>();
			}
			private set
			{
				_userManager = value;
			}
		} 

		[HttpPost]
		[AllowAnonymous]
		[Route("api/Authenticate")]
		public String Authenticate(APIAuthenticateViewModel login)
		{

			if(login == null || string.IsNullOrEmpty(login.Name) || string.IsNullOrEmpty(login.Password))
				return "failed";
			var userIdentity = UserManager.FindAsync(login.Name, login.Password).Result;
			if(userIdentity != null)
			{
				var identity = new ClaimsIdentity(Startup.OAuthBearerOptions.AuthenticationType);
				identity.AddClaim(new Claim(ClaimTypes.Name, login.Name));
				identity.AddClaim(new Claim(ClaimTypes.NameIdentifier, userIdentity.Id));
				AuthenticationTicket ticket = new AuthenticationTicket(identity, new AuthenticationProperties());
				var currentUtc = new SystemClock().UtcNow;
				ticket.Properties.IssuedUtc = currentUtc;
				ticket.Properties.ExpiresUtc = currentUtc.Add(TimeSpan.FromMinutes(30));
				string AccessToken = Startup.OAuthBearerOptions.AccessTokenFormat.Protect(ticket);
				return AccessToken;
			}
			return "failed";
		}

		[Authorize]
		[HttpGet]
		[Route("api/ValidateToken")]
		public String ValidateToken()
		{
			var user = this.User.Identity;
			if(user != null)
				return string.Format("{0} - {1}", user.GetUserId(), user.GetUserName());
			else
				return "Unable to resolve user id";

		}
    }
}
