using System;
using UIKit;
using Foundation;

namespace StopWatchApp.iOS.Views
{
	public class LapTableSource : UITableViewSource 
	{
		private string[] _tableItems;
		private string _cellIdentifier = "LapTableCell";

		public LapTableSource (string[] items)
		{
			_tableItems = items;
		}

		public override nint RowsInSection(UITableView tableview, nint section)
		{
			return _tableItems.Length;
		}

		public override UITableViewCell GetCell (UITableView tableView, NSIndexPath indexPath)
		{
			var cell = tableView.DequeueReusableCell(_cellIdentifier);
			var item = _tableItems[indexPath.Row];

			if (cell == null) { 
				cell = new UITableViewCell (UITableViewCellStyle.Default, _cellIdentifier); 
			}

			cell.TextLabel.Text = item;

			return cell;
		}
	}
}

