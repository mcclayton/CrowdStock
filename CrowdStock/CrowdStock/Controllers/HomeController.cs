using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace CrowdStock.Controllers
{
	public class HomeController : Controller
	{
		public ActionResult Index()
		{
			return View();
		}

		public ActionResult About()
		{
			ViewBag.Message = "CrowdStock";

			return View();
		}

		public ActionResult Contact()
		{
			ViewBag.Message = "Contact Us";

			return View();
		}
	}
}