using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using PagedList;
using CrowdStock.Models;

namespace CrowdStock.Controllers
{
	[Authorize(Users="bill@billking.io")]
	public class AdminController : Controller
	{
		CrowdStockDBContext db = new CrowdStockDBContext();

		public ActionResult Index(int? page)
		{
			return View(db.Users.OrderBy(u => u.Id).ToPagedList(page ?? 1, 25));
		}
	}
}