require 'time'
require 'optparse'
require 'optparse/time'

class Position
  attr_reader :x, :y, :z

  def initialize(pos)
    @x = pos[0]
    @y = pos[1]
    @z = pos[2]
  end

  def pos
    [@x, @y, @z]
  end

  def is_within_box(corner1, corner2)
    return false if @x < corner1.x or @x > corner2.x
    return false if @y < corner1.y or @y > corner2.y
    return false if @z < corner1.z or @z > corner2.z
    return true
  end
end

class Block
  attr_reader :item, :pos

  def initialize(item, pos)
    @item = item
    @pos = pos
  end

  def to_s
    pad = [25 - item.length, 0].max
    "#{item}#{' ' * pad} at #{pos.pos.join ', '}"
  end
end

class BlockHistory
  attr_reader :time, :player, :state, :block

  def initialize(time, player, state, block)
    @time = time
    @player = player
    @state = state
    @block = block
  end

  def to_s
    pad1 = ['accessed'.length - state.length, 0].max
    pad2 = [16 - player.length, 0].max
    "#{time.asctime}: #{player}#{' ' * pad2} #{state}#{' ' * pad1} #{block}"
  end

  def self.parse(str)
    parts = str.split ' '

    pos_array = parts[9..11].map(&:to_i)
    pos = Position.new pos_array

    item = parts[8]
    block = Block.new item, pos

    time_str = parts[0..5].join ' '
    time = Time.parse time_str

    player = parts[6]
    state = parts[7].to_sym
    BlockHistory.new time, player, state, block
  end
end

options = {} 

def process(options, bhlog)
  block_history_log = bhlog.dup

  if options.has_key? :time_older
    time = options[:time_older]
    block_history_log.select! { |log| log.time > time }
  end
  if options.has_key? :time_earlier
    time = options[:time_earlier]
    block_history_log.select! { |log| log.time < time }
  end
  if options.has_key? :player
    player = options[:player]
    block_history_log.select! { |log| log.player == player }
  end
  if options.has_key? :state
    state = options[:state]
    block_history_log.select! { |log| log.state.to_s == state }
  end
  if options.has_key? :item
    item = options[:item]
    block_history_log.select! { |log| log.block.item == item }
  end
  if options.has_key?(:pos1) and options.has_key?(:pos2)
    pos1 = Position.new(options[:pos1].split('.').map(&:to_i))
    pos2 = Position.new(options[:pos2].split('.').map(&:to_i))
    puts "#{pos1.x} #{pos1.y} #{pos1.z} #{pos1.x} #{pos1.y} #{pos1.z}"
    block_history_log.select! { |log| log.block.pos.is_within_box(pos1, pos2) }
  end

  puts block_history_log.join "\n"
end

parser = OptionParser.new do |opts|
  opts.banner = 'Usage: example.rb <block history logfile> [options]'

  opts.on('-f STR', '--file STR', String, 'Specify a specific logfile') do |f|
    options[:file] = f
  end

  opts.on('-to TIME', '--time-older TIME', Time, 'Find logs with timestamps older than') do |t|
    options[:time_older] = t
  end

  opts.on('-te TIME', '--time-earlier TIME', Time, 'Find logs with timestamps earlier than') do |t|
    options[:time_earlier] = t
  end

  opts.on('-p STR', '--player STR', 'Find logs with a specific player') do |p|
    options[:player] = p
  end

  opts.on('-s STR', '--state STR', 'Find logs with a specific state') do |s|
    options[:state] = s
  end

  opts.on('-i STR', '--item STR', 'Find logs with a specific item') do |i|
    options[:item] = i
  end

  opts.on("--pos1 STR", 'Find logs with positions greater than') do |p1|
    options[:pos1] = p1
  end
  
  opts.on("--pos2 NUM", 'Find logs with positions lesser than') do |p2|
    options[:pos2] = p2
  end

  opts.on('--interactive', 'Start in interactive mode') do |inter|
    options[:interactive] = inter
  end
end
parser.parse(ARGV)

if not options.has_key? :file
  puts "You must specify a block history logfile!"
  exit
end

block_history_log = []
File.open(options[:file], 'r') do |file|
  file.each_line.with_index do |line, i|
    block_history_log << BlockHistory.parse(line)
    puts "Read #{i} logs" if i % 1000 == 0 and i > 0
  end
end
puts "Read #{block_history_log.length} logs"

if options.has_key? :interactive and options[:interactive]
  loop do
    begin
      print "#{options[:file]}> "
      input = STDIN.gets.chomp.split ' '
      exit if input[0] == 'exit'

      options = { file: options[:file] }
      parser.parse(input)
      process options, block_history_log
    rescue Interrupt
      exit
    end
  end
else
  process options, block_history_log
end
