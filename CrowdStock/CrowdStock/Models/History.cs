using Newtonsoft.Json;
using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Runtime.Serialization;

namespace CrowdStock.Models
{
	public class History
	{
		[Key]
		[Required]
		public int Id { get; set; }

		[Required]
		[StringLength(4)]
		public string StockId { get; set; }

		[Required]
		[Index]
		public DateTime Date { get; set; }

		[Required]
		public float Value { get; set; }

		[JsonIgnore] //json
		[IgnoreDataMember] //xml
		public virtual Stock Stock { get; set; }
	}
}