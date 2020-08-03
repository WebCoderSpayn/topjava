$(function () {
    makeEditable({
            ajaxUrl: "profile/meals/",
            datatableApi: $("#datatable").DataTable({
                "paging": false,
                "info": true,
                "columns": [
                    {
                        "data": "dateTime"
                    },
                    {
                        "data": "description"
                    },
                    {
                        "data": "calories"
                    },
                    {
                        "defaultContent": "Edit",
                        "orderable": false
                    },
                    {
                        "defaultContent": "Delete",
                        "orderable": false
                    }
                ],
                "order": [
                    [
                        0,
                        "desc"
                    ]
                ]
            })
        }
    );
});

function clearFilter() {
    $("#filter").trigger('reset');
    dataFilter.length = 0;
    updateTable();
}

function filterTable() {
    form = $("#filter");
    $.ajax({
        type: "GET",
        url: context.ajaxUrl + "filter",
        data: form.serialize()
    }).done(function (result) {
        dataFilter = result;
        updateTable();
        successNoty("found result: " + dataFilter.length);
    });
}