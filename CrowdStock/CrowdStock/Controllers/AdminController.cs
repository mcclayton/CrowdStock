using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using PagedList;
using CrowdStock.Models;
using Microsoft.AspNet.Identity;

namespace CrowdStock.Controllers
{
	[Authorize(Roles = "Administrator")]
	public class AdminController : Controller
	{
		CrowdStockDBContext db = new CrowdStockDBContext();

		public ActionResult Index()
		{
			return View();
		}

		public ActionResult Users(int? page)
		{
			return View(db.Users.OrderBy(u => u.LastName).ThenBy(u => u.FirstName).ToPagedList(page ?? 1, 25));
		}

		public ActionResult UpdateReputations()
		{
			var users = db.Users;
			foreach(var user in users)
			{
				user.UpdateReputation();
			}
			db.SaveChanges();
			return RedirectToAction("Index");
		}
	}
}