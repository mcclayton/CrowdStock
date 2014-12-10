using CrowdStock.Models;
using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.Owin;
using Microsoft.Owin.Infrastructure;
using Microsoft.Owin.Security;
using System;
using System.Net;
using System.Security.Claims;
using System.Threading.Tasks;
using System.Web;
using System.Web.Http;
using Postal;
using System.Net.Http;

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
		public String Authenticate(ApiAuthenticateViewModel login)
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

		[HttpPost]
		public async Task<IHttpActionResult> Register(ApiRegisterViewModel model)
		{
			ApplicationUser user = new ApplicationUser
			{
				UserName = model.UserName,
				FirstName = model.FirstName,
				LastName = model.LastName,
				Email = model.Email,
				DateRegistered = DateTime.Now
			};

			var result = await UserManager.CreateAsync(user, model.Password);
			if(result.Succeeded)
			{
				//TODO: fix this somehow 
				//string code = UserManager.GenerateEmailConfirmationTokenAsync(user.Id).Result;
				//var callbackUrl = Url.Route("Default", new { action="ConfirmEmail", controller="Account", userId = user.Id, code = code });
				//SendEmail(user, "Confirm your account", "Please confirm your account by clicking <a href=\"" + callbackUrl + "\">here</a>");

				var newUser = await UserManager.FindByEmailAsync(model.Email);
				if(newUser != null)
					return Ok(newUser.Id);
				else
					throw new HttpResponseException(new HttpResponseMessage
					{
						Content = new StringContent("Error occurred during registration. Please try again later."),
						StatusCode = HttpStatusCode.InternalServerError
					});
			}

			var enumerator = result.Errors.GetEnumerator();
			enumerator.MoveNext();
			throw new HttpResponseException(new HttpResponseMessage { 
				Content = new StringContent(enumerator.Current),
				StatusCode = HttpStatusCode.InternalServerError
			});
		}
	}
}