//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace CrowdStockDBUpdater
{
    using System;
    using System.Collections.Generic;
    
    public partial class Vote
    {
        public int Id { get; set; }
        public string UserId { get; set; }
        public string StockId { get; set; }
        public bool isPositive { get; set; }
        public System.DateTime Date { get; set; }
        public System.DateTime EndDate { get; set; }
    
        public virtual AspNetUser AspNetUser { get; set; }
        public virtual Stock Stock { get; set; }
    }
}
