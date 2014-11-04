using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using CrowdStock.Models;

namespace CrowdStock.Controllers
{
	public class UsersController : Controller
	{
		private CrowdStockDBContext db = new CrowdStockDBContext();

		// GET: Users
		public ActionResult Index()
		{
			ViewBag.TopUsers = db.Users.OrderByDescending(u => u.Reputation).Take(100);
			return View();
		}

		// GET: Users/Details/5
		public ActionResult Details(string id)
		{
			if(id == null)
			{
				return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
			}
			ApplicationUser applicationUser = db.Users.Find(id);
			if(applicationUser == null)
			{
				return HttpNotFound();
			}
			return View(applicationUser);
		}

		protected override void Dispose(bool disposing)
		{
			if(disposing)
			{
				db.Dispose();
			}
			base.Dispose(disposing);
		}
	}
}
