let mealAjaxUrl = "profile/meals/";

function updateFilteredTable() {
    $.ajax({
        type: "GET",
        url: "profile/meals/filter",
        data: $("#filter").serialize()
    }).done(updateTableByData);
}

$.ajaxSetup({
    converters: {
        "text json": function (stringData) {
            let json = JSON.parse(stringData);
            $(json).each(function () {
                this.dateTime = this.dateTime.replace('T', ' ').substr(0, 16);
            });
            return json;
        }
    }
});

function clearFilter() {
    $("#filter")[0].reset();
    $.get("profile/meals/", updateTableByData);
}

$(function () {
    makeEditable({
        ajaxUrl: "profile/meals/",
        datatableApi: $("#datatable").DataTable({
            "ajax": {
                "url": mealAjaxUrl,
                "dataSrc": ""
            },
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime",
                    "render": function (date, type, row) {
                        if (type === "display") {
                            return date.replace('T', ' ');
                        }
                        return date;
                    }
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false,
                    "render": renderEditBtn
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false,
                    "render": renderDeleteBtn
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ],
            "createdRow": function (row, data, dataIndex) {
                $(row).attr("data-mealexcess", data.excess);
            }
        }),
        updateTable: updateFilteredTable
    });
});

let startDate = $('#startDate');
let endDate = $('#endDate');

startDate.datetimepicker({
    timepicker: false,
    format: 'Y-m-d',
    formatDate: 'Y-m-d',
    onShow: function () {
        this.setOptions({
            maxDate: endDate.val() ? endDate.val() : false
        })
    }
});

endDate.datetimepicker({
    timepicker: false,
    format: 'Y-m-d',
    formatDate: 'Y-m-d',
    onShow: function () {
        this.setOptions({
            minDate: startDate.val() ? startDate.val() : false
        })
    }
});

let startTime = $('#startTime');
let endTime = $('#endTime');

startTime.datetimepicker({
    datepicker: false,
    format: 'H:i',
    onShow: function () {
        this.setOptions({
            maxTime: endTime.val() ? endTime.val() : false
        })
    }
});

endTime.datetimepicker({
    datepicker: false,
    format: 'H:i',
    onShow: function () {
        this.setOptions({
            minTime: startTime.val() ? startTime.val() : false
        })
    }
});

$('#dateTime').datetimepicker({
    format:'Y-m-d H:i',
    lang:'ru'
});