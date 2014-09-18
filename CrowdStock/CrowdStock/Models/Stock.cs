using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace CrowdStock.Models
{
	public class Stock
	{
		[Key]
		[Required]
		[StringLength(4)]
		[Display(Name="Symbol")]
		public string ID { get; set; }

		[Required]
		[StringLength(50)]
		public string Name { get; set; }

		public string Description { get; set; }

		public virtual ICollection<History> Histories { get; set; }
	}
}