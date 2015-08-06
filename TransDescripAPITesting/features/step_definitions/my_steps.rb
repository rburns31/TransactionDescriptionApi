require 'json'

Given(/^The client can access the Transaction Description API$/) do
  puts 'given the client can access the Transaction Description API'
end

When(/^a HTTP create request is made with the URI "([^"]*)"$/) do |uri|
  puts "a HTTP create request is made with the URI #{uri}"
  @uri = uri
  @request = RestClient::Resource.new(
      uri,
      :headers => {'api-key' => 'MOBILE', 'User-Id' => 'abc123', 'Accept' => 'application/json;v=3'}
  )
  begin
    @response = @request.post('{ }')
  rescue RestClient::ResourceNotFound
    @code = 404
  rescue RestClient::BadRequest
    @code = 400
  rescue RestClient::Conflict
    @code = 409
  ensure
    if (@response != nil)
      @code = @response.code
    end
  end
end

When(/^a HTTP read request is made with the URI "([^"]*)"$/) do |uri|
  puts "a HTTP read request is made with the URI #{uri}"
  @uri = uri
  @request = RestClient::Resource.new(
      uri,
      :headers => {'api-key' => 'MOBILE', 'User-Id' => 'abc123', 'Accept' => 'application/json;v=3'}
  )
  begin
    @response = @request.get
  rescue RestClient::ResourceNotFound
    @code = 404
  rescue RestClient::BadRequest
    @code = 400
  ensure
    if (@response != nil)
      @code = @response.code
    end
  end
end

When(/^a HTTP update request is made with the URI "([^"]*)"$/) do |uri|
  puts "a HTTP update request is made with the URI #{uri}"
  @uri = uri
  @request = RestClient::Resource.new(
      uri,
      :headers => {'api-key' => 'MOBILE', 'User-Id' => 'abc123', 'Accept' => 'application/json;v=3'}
  )
  begin
    @response = @request.put('{ }')
  rescue RestClient::ResourceNotFound
    @code = 404
  rescue RestClient::BadRequest
    @code = 400
  ensure
    if (@response != nil)
      @code = @response.code
    end
  end
end

When(/^a HTTP delete request is made with the URI "([^"]*)"$/) do |uri|
  puts "a HTTP delete request is made with the URI #{uri}"
  @uri = uri
  @request = RestClient::Resource.new(
      uri,
      :headers => {'api-key' => 'MOBILE', 'User-Id' => 'abc123', 'Accept' => 'application/json;v=3'}
  )
  begin
    @response = @request.delete
  rescue RestClient::ResourceNotFound
    @code = 404
  rescue RestClient::BadRequest
    @code = 400
  ensure
    if (@response != nil)
      @code = @response.code
    end
  end
end

Then(/^the HTTP response code will be "([^"]*)"$/) do |code|
  if (code.to_s != @code.to_s)
    puts("For URL = #{@uri} the expected response status code = #{code}. Actual status code = #{@code}")
    raise "Invalid HTTP_STATUS Code: #{@code}"
  end
end

Then (/^the transaction description response will be "([^"]*)"$/) do |tDescription|
  if (@response != nil)
    @tDescription = @response.split('"')[3]
  else
    @tDescription = ''
  end
  if (tDescription.to_s != @tDescription.to_s)
    puts("For URL = #{@uri} the expected response = #{tDescription}. Actual response = #{@tDescription}")
    raise "Invalid Transaction Description: #{@tDescription}"
  end
end