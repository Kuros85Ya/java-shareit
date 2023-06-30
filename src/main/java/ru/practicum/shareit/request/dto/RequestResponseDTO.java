package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RequestResponseDTO {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private List<RequestedItemResponseDto> items;
}

//    var created = new Date(jsonData[0].created);
//    var future = new Date(pm.environment.get('currentDateTime'));
//    pm.expect(created, '"created"  must be < ' + pm.environment.get('currentDateTime')).to.lte(future);
//});
//pm.test("Test item request[0] 'items' field", function () {
//    var jsonData = pm.response.json();
//    pm.expect(jsonData[0]).to.have.property('items');
//    pm.expect(jsonData[0].items, '"items" must be not null').is.not.null;
//    pm.expect(jsonData[0].items.length, 'items count must be 1').to.eql(1);
//});
//pm.test("Test request[0].items[0] 'id' field", function () {
//    var jsonData = pm.response.json();
//    pm.expect(jsonData[0].items[0]).to.have.property('id');
//    pm.expect(jsonData[0].items[0].id, '"id" must be 5').to.eql(5);
//});
//pm.test("Test request[0].items[0] 'name' field", function () {
//    var jsonData = pm.response.json();
//    pm.expect(jsonData[0].items[0]).to.have.property('name');
//    pm.expect(jsonData[0].items[0].name, '"name" must be "Щётка для обуви"').to.eql('Щётка для обуви');
//});
//pm.test("Test request[0].items[0] 'description' field", function () {
//    var jsonData = pm.response.json();
//    pm.expect(jsonData[0].items[0]).to.have.property('description');
//    pm.expect(jsonData[0].items[0].description, '"description" must be "Стандартная щётка для обуви"').to.eql('Стандартная щётка для обуви');
//});
//pm.test("Test request[0].items[0] 'available' field", function () {
//    var jsonData = pm.response.json();
//    pm.expect(jsonData[0].items[0]).to.have.property('available');
//    pm.expect(jsonData[0].items[0].available, '"available" must be "true"').to.true;
//});
//pm.test("Test request[0].items[0] 'requestId' field", function () {
//    var jsonData = pm.response.json();
//    pm.expect(jsonData[0].items[0]).to.have.property('requestId');
//    pm.expect(jsonData[0].items[0].requestId, '"requestId" must be "1"').to.eq(1);
