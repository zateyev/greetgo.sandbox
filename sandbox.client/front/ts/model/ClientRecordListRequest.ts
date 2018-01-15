import {ColumnSortType} from "./ColumnSortType";

export class ClientRecordListRequest {
  public clientRecordCountToSkip: number/*long*/;
  public clientRecordCount: number/*long*/;
  public columnSortType: ColumnSortType;
  public sortAscend: boolean;
}
