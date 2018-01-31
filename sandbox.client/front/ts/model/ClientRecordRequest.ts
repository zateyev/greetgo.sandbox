import {ColumnSortType} from "./ColumnSortType";

export class ClientRecordRequest {
  public clientRecordCountToSkip: number/*long*/;
  public clientRecordCount: number/*long*/;
  public columnSortType: ColumnSortType;
  public sortAscend: boolean;
  public nameFilter: string;
}
